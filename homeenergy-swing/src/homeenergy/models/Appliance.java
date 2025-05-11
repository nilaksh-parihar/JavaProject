
package homeenergy.models;

public abstract class Appliance implements EnergyConsumable {
    protected String name;
    protected int power;
    protected int hours;

    public Appliance(String name, int power, int hours) {
        this.name = name;
        this.power = power;
        this.hours = hours;
    }
    
    @Override
    public String getData() {
        return name + " - " + power + "W - " + hours + "h/day - " + calculateEnergy() + " kWh/day";
    }
}