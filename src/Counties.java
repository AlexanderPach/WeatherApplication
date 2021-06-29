public class Counties {

    public String sNum;
    public String cNum;
    public String cName;

    public Counties(String sNum, String cNum, String cName) {
        this.sNum = sNum;
        this.cNum = cNum;
        this.cName = cName;
    }

    public String getsNum() {
        return sNum;
    }

    public String getcNum() {
        return cNum;
    }

    public String getcName() {
        return cName;
    }

    public void setsNum(String sNum) {
        this.sNum = sNum;
    }

    public void setcNum(String cNum) {
        this.cNum = cNum;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }
}
