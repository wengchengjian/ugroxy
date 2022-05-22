package com.weng.ugroxy.sh.command;

import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * @Author 翁丞健
 * @Date 2022/5/22 16:28
 * @Version 1.0.0
 */
@ShellComponent
@ShellCommandGroup("local-proxy")
public class LocalProxyCommand {


    @ShellMethod("connect to local server")
    public String connect(Integer port){

    }
}
