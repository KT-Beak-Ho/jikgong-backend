package jikgong.domain.jobpost.entity;

import lombok.Getter;

@Getter
public enum Tech {

    NORM("보통 인부"), // Normal
    TILE("타일공"), // Tile
    FMAN("작업반장"), // Foreman
    SKLB("특별인부"), // Skilled Laborer
    HELP("조력공"), // Helper
    SCAF("비계공"), // Scaffolder
    FWC("형틀목공"), // Formwork Carpenter
    REBW("철근공"), // Rebar Worker
    SSTW("철골공"), // Steel Structure Worker
    WELD("용접공"), // Welder
    CONC("콘크리트공"), // Concrete Worker
    BRICK("조적공"), // Bricklayer
    DRWF("견출공"), // Drywall Finisher
    CCON("건축목공"), // Construction Carpenter
    WIND("창호공"), // Window Door Installer
    GLAZ("유리공"), // Glazier
    WTRP("방수공"), // Waterproofing Worker
    PLAS("미장공"), // Plasterer
    PAINT("도장공"), // Painter
    INTF("내장공"), // Interior Finisher
    WALL("도배공"), // Wallpaper Installer
    POLI("연마공"), // Polisher
    STON("석공"), // Stonemason
    GROUT("줄눈공"), // Grout Worker
    PNAS("판넬조립공"), // Panel Assembler
    ROOF("지붕잇기공"), // Roofer
    LAND("조경공"), // Landscaper
    CAULK("코킹공"), // Caulker
    PLUM("배관공"), // Plumber
    BOIL("보일러공"), // Boiler Technician
    SANI("위생공"), // Sanitary Technician
    DUCT("덕트공"), // Duct Installer
    INSW("보온공"), // Insulation Worker
    MEQT("기계설비공"), // Mechanical Equipment Technician
    ELEC("내선전공"), // Electrician
    TCOM("통신내선공"), // Telecommunications Installer
    TEQ("통신설비공"); // Telecommunications Equipment Installer
    private final String description;

    // todo: 추가 예정
    Tech(String description) {
        this.description = description;
    }
}
