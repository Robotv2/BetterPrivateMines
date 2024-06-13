package fr.robotv2.betterprivatemines.util;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class FileUtil {
    public String getNameWithoutExtension(final File file) {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }
}
