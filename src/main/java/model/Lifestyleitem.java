package model;

public class Lifestyleitem {

    private  String  txt;
    private String type;
    private String brf;

    public Lifestyleitem(String txt, String type, String brf) {
        this.txt = txt;
        this.type = type;
        this.brf = brf;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrf() {
        return brf;
    }

    public void setBrf(String brf) {
        this.brf = brf;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Lifestyleitem{");
        sb.append("txt='").append(txt).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", brf='").append(brf).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
