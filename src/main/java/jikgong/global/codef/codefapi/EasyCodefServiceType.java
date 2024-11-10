package jikgong.global.codef.codefapi;

public enum EasyCodefServiceType {
    SANDBOX(2),
    DEMO(1),
    API(0);

    private int serviceType;

    private EasyCodefServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    protected int getServiceType() {
        return serviceType;
    }

}
