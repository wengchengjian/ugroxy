package com.weng.ugroxy.sh.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * @Author 翁丞健
 * @Date 2022/5/21 23:05
 * @Version 1.0.0
 */
@ShellComponent
public class TranslateCommand {
    @ShellMethod("translate text to other language")
    public String translate(String text){
        return text.toUpperCase();
    }
}
