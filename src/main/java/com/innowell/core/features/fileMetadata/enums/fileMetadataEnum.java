package com.innowell.core.features.fileMetadata.enums;

public enum fileMetadataEnum {
    FLOOR_DOCUMENTS;
    
    public static boolean isValidSource(String source) {
        try {
            fileMetadataEnum.valueOf(source);
            return true;
        } 
        catch (IllegalArgumentException e) {
            return false;
        }
    }

}
