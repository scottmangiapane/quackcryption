package co.twoduck.quackcryption;

public class SingleStep {
    public String toAES(String key, String initVector, String str1) {
        return AES.encrypt(key, initVector, str1);
    }

    public String fromAES(String key, String initVector, String str2) {
        return AES.decrypt(key, initVector, str2);
    }

    public String toQuack(String str3) {
        return Quack.quackCrypt(str3);
    }

    public String fromQuack(String str4) {
        return Quack.quackReader(str4);
    }

    public String throughQuack(String key, String initVector, String str5) {
        return toQuack(toAES(key, initVector, str5));
    }

    public String throughNormal(String key, String initVector, String str6) {
        return fromAES(key, initVector, fromQuack(str6));
    }
}
