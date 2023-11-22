package hr.kingict.springbootakademija2023.dto;

public class LocationDto {

    private String iataCode;
    private String name;
    private String detailedName;


    public String getIataCode() {
        return iataCode;
    }

    public String getDetailedName() {
        return detailedName;
    }

    public void setDetailedName(String detailedName) {
        this.detailedName = detailedName;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
