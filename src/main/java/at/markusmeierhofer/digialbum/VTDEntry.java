package at.markusmeierhofer.digialbum;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Created by MM on 12.02.2017.
 */
public class VTDEntry implements Serializable {
    private static final long serialVersionUID = 42L;

    private transient StringProperty headerProperty = new SimpleStringProperty();
    private transient StringProperty imageUrlProperty = new SimpleStringProperty();
    private transient StringProperty textProperty = new SimpleStringProperty();

    private String header = "";
    private String imageUrl = "";
    private String text = "";

    public VTDEntry(String header, String imageUrl, String text) {
        this.headerProperty.setValue(header);
        this.imageUrlProperty.setValue(imageUrl);
        this.textProperty.setValue(text);
    }

    public VTDEntry() {
    }

    public StringProperty getHeader() {
        return headerProperty;
    }

    public void setHeader(String header) {
        this.headerProperty.setValue(header);
    }

    public StringProperty getImageUrl() {
        return imageUrlProperty;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrlProperty.setValue(imageUrl);
    }

    public StringProperty getText() {
        return textProperty;
    }

    public void setText(String text) {
        this.textProperty.setValue(text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VTDEntry vtdEntry = (VTDEntry) o;

        if (!headerProperty.getValueSafe().equals(vtdEntry.headerProperty.getValueSafe())) return false;
        if (!imageUrlProperty.getValueSafe().equals(vtdEntry.imageUrlProperty.getValueSafe())) return false;
        return textProperty.getValueSafe().equals(vtdEntry.textProperty.getValueSafe());
    }

    @Override
    public int hashCode() {
        int result = headerProperty.hashCode();
        result = 31 * result + imageUrlProperty.hashCode();
        result = 31 * result + textProperty.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.headerProperty.getValue() != null ? this.headerProperty.getValue() : "Unbenannt";
    }

    public void save() {
        this.header = this.headerProperty.getValue();
        this.imageUrl = this.imageUrlProperty.getValue();
        this.text = this.textProperty.getValue();
    }

    public void read() {
        this.headerProperty = new SimpleStringProperty();
        this.imageUrlProperty = new SimpleStringProperty();
        this.textProperty = new SimpleStringProperty();

        this.headerProperty.setValue(this.header);
        this.imageUrlProperty.setValue(this.imageUrl);
        this.textProperty.setValue(this.text);
    }
}
