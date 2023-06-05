package es.pfc.dacloud.business.dto;

public class NewFolderDTO {

    private Long idDirectorioPadre;
    private String nombreDirectorio;

    public Long getIdDirectorioPadre() {
        return idDirectorioPadre;
    }

    public void setIdDirectorioPadre(Long idDirectorioPadre) {
        this.idDirectorioPadre = idDirectorioPadre;
    }

    public String getNombreDirectorio() {
        return nombreDirectorio;
    }

    public void setNombreDirectorio(String nombreDirectorio) {
        this.nombreDirectorio = nombreDirectorio;
    }

}
