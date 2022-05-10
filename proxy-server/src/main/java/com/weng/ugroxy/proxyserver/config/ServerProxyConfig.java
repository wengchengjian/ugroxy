package com.weng.ugroxy.proxyserver.config;

import com.google.gson.reflect.TypeToken;
import com.weng.ugroxy.proxycommon.protocol.client.Client;
import com.weng.ugroxy.proxycommon.protocol.client.ClientProxyMapping;
import com.weng.ugroxy.proxycommon.support.Config;
import com.weng.ugroxy.proxycommon.support.ProxyConfigApplicationEvent;
import com.weng.ugroxy.proxycommon.utils.ApplicationContextUtil;
import com.weng.ugroxy.proxycommon.utils.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 翁丞健
 * @Date 2022/4/30 21:39
 * @Version 1.0.0
 */
@Slf4j
@Data
public class ServerProxyConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String CONFIG_FILE_NAME;

    static {
        String dataPath = System.getProperty("user.home")+"/"+".ugroxy";

        File file = new File(dataPath);
        if(!file.exists()){
            file.mkdir();
        }

        CONFIG_FILE_NAME = dataPath + "/config.json";
    }

    private String serverHost;

    private Integer serverPort;

    /** 配置服务绑定主机host */
    private String configServerBind;

    /** 配置服务端口 */
    private Integer configServerPort;

    /** 配置服务管理员用户名 */
    private String configAdminUsername;

    /** 配置服务管理员密码 */
    private String configAdminPassword;

    private List<Client> clients;

    /** 更新配置后保证在其他线程即时生效 */
    private static final ServerProxyConfig instance = new ServerProxyConfig();

    /** 代理服务器为各个代理客户端（key）开启对应的端口列表（value） */
    private final Map<String,List<Integer>> clientInetPortMap = new ConcurrentHashMap<>();

    /** 代理服务器上的每个对外端口（key）对应的代理客户端背后的真实服务器信息（value） */
    private final Map<Integer,String> inetPortNetInfoMap = new ConcurrentHashMap<>();

    private ServerProxyConfig(){
        // 代理服务器主机和端口配置初始化
        this.serverPort = Config.getInstance().getIntProperty("server.port",3300);
        this.serverHost = Config.getInstance().getStringProperty("server.bind", "0.0.0.0");

        // 配置服务器主机和端口配置初始化
        this.configServerPort = Config.getInstance().getIntProperty("config.server.port",3400);
        this.configServerBind = Config.getInstance().getStringProperty("config.server.bind", "0.0.0.0");

        // 配置服务器管理员登录认证信息
        this.configAdminUsername = Config.getInstance().getStringProperty("config.admin.username","ugroxy");
        this.configAdminPassword = Config.getInstance().getStringProperty("config.admin.password","ugroxy");
        log.info(
                "config init serverHost {}, serverPort {}, configServerBind {}, configServerPort {}, configAdminUsername {}, configAdminPassword {}",
                serverHost, serverPort, configServerBind, configServerPort, configAdminUsername, configAdminPassword);

        update(null);
    }

    public static ServerProxyConfig getInstance(){
        return instance;
    }
    public void update(String jsonConfig){
        File file = new File(CONFIG_FILE_NAME);

        try {
            if(jsonConfig == null && file.exists()){
                InputStream in = new BufferedInputStream(new FileInputStream(file));

                byte[] buf = new byte[4096];

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int readIndex = 0;
                while((readIndex = in.read(buf)) != -1){
                    out.write(buf,0,readIndex);
                }
                in.close();
                jsonConfig = new String(out.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (FileNotFoundException e) {
            log.error("config file not found",e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("config file read error",e);
            throw new RuntimeException(e);
        }

        List<Client> clients = JsonUtil.json2object(jsonConfig,new TypeToken<List<Client>>(){});

        if(clients==null){
            clients = new ArrayList<>();
        }

        for(Client client : clients){
            String clientKey = client.getClientKey();

            if(inetPortNetInfoMap.containsKey(clientKey)){
                log.error("密钥同时作为客户端标识，不能重复：{}",clientKey);
                throw new IllegalArgumentException("密钥同时作为客户端标识，不能重复： " + clientKey);
            }

            List<ClientProxyMapping> proxyMappings = client.getProxyMappings();

            List<Integer> ports = new ArrayList<>();

            for(ClientProxyMapping proxyMapping : proxyMappings){
                Integer port = proxyMapping.getPort();

                ports.add(port);
                if (inetPortNetInfoMap.containsKey(port)) {
                    throw new IllegalArgumentException("一个公网端口只能映射一个后端信息，不能重复: " + port);
                }
                inetPortNetInfoMap.put(port, proxyMapping.getNetInfo());
            }

            clientInetPortMap.put(clientKey, ports);

        }

        if (jsonConfig != null) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                out.write(jsonConfig.getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        ApplicationContext context = ApplicationContextUtil.getContext();
        // 发布配置更新事件
        context.publishEvent(new ProxyConfigApplicationEvent(jsonConfig));
    }

    public List<Integer> getClientInetPorts(String clientKey){
        return clientInetPortMap.get(clientKey);
    }

}
