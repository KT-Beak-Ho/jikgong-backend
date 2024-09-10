package jikgong.domain.workexperience.entity;

import lombok.Getter;

@Getter
public enum Tech {

    NORMAL("보통인부"), // Normal
    FOREMAN("작업반장"), // Foreman
    SKILLED_LABORER("특별인부"), // Skilled Laborer
    HELPER("조력공"), // Helper
    SCAFFOLDER("비계공"), // Scaffolder
    FORMWORK_CARPENTER("형틀목공"), // Formwork Carpenter
    REBAR_WORKER("철근공"), // Rebar Worker
    STEEL_STRUCTURE_WORKER("철골공"), // Steel Structure Worker
    WELDER("용접공"), // Welder
    CONCRETE_WORKER("콘크리트공"), // Concrete Worker
    BRICKLAYER("조적공"), // Bricklayer
    DRYWALL_FINISHER("견출공"), // Drywall Finisher
    CONSTRUCTION_CARPENTER("건축목공"), // Construction Carpenter
    WINDOW_DOOR_INSTALLER("창호공"), // Window Door Installer
    GLAZIER("유리공"), // Glazier
    WATERPROOFING_WORKER("방수공"), // Waterproofing Worker
    PLASTERER("미장공"), // Plasterer
    TILE("타일공"), // Tile
    PAINTER("도장공"), // Painter
    INTERIOR_FINISHER("내장공"), // Interior Finisher
    WALLPAPER_INSTALLER("도배공"), // Wallpaper Installer
    POLISHER("연마공"), // Polisher
    STONEMASON("석공"), // Stonemason
    GROUT_WORKER("줄눈공"), // Grout Worker
    PANEL_ASSEMBLER("판넬조립공"), // Panel Assembler
    ROOFER("지붕잇기공"), // Roofer
    LANDSCAPER("조경공"), // Landscaper
    CAULKER("코킹공"), // Caulker
    PLUMBER("배관공"), // Plumber
    BOILER_TECHNICIAN("보일러공"), // Boiler Technician
    SANITARY_TECHNICIAN("위생공"), // Sanitary Technician
    DUCT_INSTALLER("덕트공"), // Duct Installer
    INSULATION_WORKER("보온공"), // Insulation Worker
    MECHANICAL_EQUIPMENT_TECHNICIAN("기계설비공"), // Mechanical Equipment Technician
    ELECTRICIAN("내선전공"), // Electrician
    TELECOMMUNICATIONS_INSTALLER("통신내선공"), // Telecommunications Installer
    TELECOMMUNICATIONS_EQUIPMENT_INSTALLER("통신설비공"); // Telecommunications Equipment Installer

    private final String description;

    Tech(String description) {
        this.description = description;
    }
}