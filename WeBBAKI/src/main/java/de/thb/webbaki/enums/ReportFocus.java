package de.thb.webbaki.enums;

public enum ReportFocus {
    UNKNOWN("unknown", "Unbekannt"), COMPANY("company", "Firmeninterner"), BRANCHE("branche", "Branchenweiter"), SECTOR("sector", "Sektorweiter"), NATIONAL("national", "Nationaler");

    private String urlPart;
    private String germanRepresentation;

    ReportFocus(String urlPart, String germanRepresentation){
        this.urlPart = urlPart;
        this.germanRepresentation = germanRepresentation;
    }

    public static ReportFocus getReportFocusByEnglishRepresentation(String representation){
        switch(representation){
            case "company":
                return COMPANY;
            case "branche":
                return BRANCHE;
            case "sector":
                return SECTOR;
            case "national":
                return NATIONAL;
            default:
                return UNKNOWN;
        }
    }

    public String getUrlPart() {
        return urlPart;
    }

    public String getGermanRepresentation() {
        return germanRepresentation;
    }
}
