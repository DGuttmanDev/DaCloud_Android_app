package es.pfc.dacloud.business.dto;

import java.io.Serializable;

public class RegistroDTO implements Serializable {

    private static final long serialVersionUID = -1932165281569098147L;

    private String nombre;
    private String apellidos;
    private String password;
    private String nick;
    private String mail;

    public RegistroDTO(String nombre, String apellidos, String password, String nick, String mail) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.password = password;
        this.nick = nick;
        this.mail = mail;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
