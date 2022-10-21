package de.thb.webbaki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SectorHealth {

    MEDIZIN("medizinische Versorgung"),
    ARZNEIUNDIMPF("Arzneimittel und Impfstoffe"),
    LABORE("Labore");


    @Getter
    private final String value;
}