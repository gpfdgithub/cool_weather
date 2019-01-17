package bean;

import org.litepal.crud.LitePalSupport;

import java.util.List;

import model.PerviewListItem;

public class WeatherInfo extends LitePalSupport {

    private String cityName;
    private String tmp;
    private String condText;
    private List<PerviewListItem> perviewListItems;

    public WeatherInfo(String cityName, String tmp, String condText, List<PerviewListItem> perviewListItems) {
        this.cityName = cityName;
        this.tmp = tmp;
        this.condText = condText;
        this.perviewListItems = perviewListItems;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getCondText() {
        return condText;
    }

    public void setCondText(String condText) {
        this.condText = condText;
    }

    public List<PerviewListItem> getPerviewListItems() {
        return perviewListItems;
    }

    public void setPerviewListItems(List<PerviewListItem> perviewListItems) {
        this.perviewListItems = perviewListItems;
    }
}
