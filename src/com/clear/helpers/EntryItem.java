package com.clear.helpers;


public class EntryItem implements Item{

	 public String title;
	 public String cantidad;
	 public int icono;
	 private boolean isSelected;
	 
	 public EntryItem(String title, String subtitle, int icono) {
	  this.title    = title;
	  this.cantidad = subtitle;
	  this.icono    = icono;
	 }
	 
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public String getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(String cantidad){
		this.cantidad = cantidad;
	}

	@Override
	public boolean isSection() {
		return false;
	}
 
}
