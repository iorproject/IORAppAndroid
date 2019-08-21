package ior.adapters;


public class FilterItem {

    private String name;
    private boolean isSelected = false;

    public FilterItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
