package es.pfc.dacloud.business.dto;

import java.io.Serializable;

public class DescargaDTO implements Serializable {

    private Long id;
    private String nombre;
    private String base64Bytes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBase64Bytes() {
        return base64Bytes;
    }

    public void setBase64Bytes(String base64Bytes) {
        this.base64Bytes = base64Bytes;
    }
}
