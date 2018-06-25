package fr.egaetan.cgbench.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ObservableValue<T> {

	T value;
	
	final PropertyChangeSupport psp = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		psp.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		psp.removePropertyChangeListener(listener);
	}
	
	public T getValue() {
		return value;
	}
	public void setValue(T newValue) {
		T oldValue = this.value;
		this.value = newValue;
		psp.firePropertyChange("value", oldValue, newValue);
	}
	public void fire() {
		psp.firePropertyChange("value", null, value);
	}
	
	
	
}
