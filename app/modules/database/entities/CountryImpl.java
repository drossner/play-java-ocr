package modules.database.entities;

import java.util.Locale;

/**
 * Created by FRudi on 06.12.2015.
 */
public enum CountryImpl {

    GERMAN(Locale.GERMAN.getDisplayName(), Locale.GERMAN.getISO3Language()), ENGLISCH(Locale.ENGLISH.getDisplayName(), Locale.ENGLISH.getISO3Language());

    CountryImpl(String name, String isoCode){
        this.name = name;
        this.isoCode = isoCode;
    }

    public static CountryImpl getEnumInstance(String value){
        value = value.toUpperCase();
        if(value.equals("GERMAN") || value.equals("DEUTSCH")){
            return CountryImpl.GERMAN;
        }else if(value.equals("ENGLISH") || value.equals("ENGLISCH")){
            return CountryImpl.ENGLISCH;
        }

        return CountryImpl.GERMAN;
    }

    private String name;

    private String isoCode;

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
