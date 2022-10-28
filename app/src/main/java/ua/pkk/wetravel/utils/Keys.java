package ua.pkk.wetravel.utils;

public enum Keys {
    VIDEO_FROM_MAP(1),
    VIDEO_FROM_ADAPTER(2),
    OWNER_ACCOUNT(3),
    LOADER_ACCOUNT(4);

    private final int value;

    Keys(int value) {
        this.value = value;
    }

    public int getIntValue() {
        return value;
    }

    public static boolean isNewDesign(){
        return true;
    }
}
