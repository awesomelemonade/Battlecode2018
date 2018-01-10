/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package bc;

public class PlanetMap {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected PlanetMap(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(PlanetMap obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        bcJNI.delete_PlanetMap(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public PlanetMap() {
    this(bcJNI.new_PlanetMap(), true);
  }

  public void validate() {
    bcJNI.PlanetMap_validate(swigCPtr, this);
  }

  public boolean onMap(MapLocation location) {
    return bcJNI.PlanetMap_onMap(swigCPtr, this, MapLocation.getCPtr(location), location);
  }

  public short isPassableTerrainAt(MapLocation location) {
    return bcJNI.PlanetMap_isPassableTerrainAt(swigCPtr, this, MapLocation.getCPtr(location), location);
  }

  public long initialKarboniteAt(MapLocation location) {
    return bcJNI.PlanetMap_initialKarboniteAt(swigCPtr, this, MapLocation.getCPtr(location), location);
  }

  public PlanetMap clone() {
    long cPtr = bcJNI.PlanetMap_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new PlanetMap(cPtr, false);
  }

  public String toJson() {
    return bcJNI.PlanetMap_toJson(swigCPtr, this);
  }

  public void setPlanet(Planet value) {
    bcJNI.PlanetMap_planet_set(swigCPtr, this, value.swigValue());
  }

  public Planet getPlanet() {
    return Planet.swigToEnum(bcJNI.PlanetMap_planet_get(swigCPtr, this));
  }

  public void setHeight(long value) {
    bcJNI.PlanetMap_height_set(swigCPtr, this, value);
  }

  public long getHeight() {
    return bcJNI.PlanetMap_height_get(swigCPtr, this);
  }

  public void setWidth(long value) {
    bcJNI.PlanetMap_width_set(swigCPtr, this, value);
  }

  public long getWidth() {
    return bcJNI.PlanetMap_width_get(swigCPtr, this);
  }

  public void setInitial_units(VecUnit value) {
    bcJNI.PlanetMap_initial_units_set(swigCPtr, this, VecUnit.getCPtr(value), value);
  }

  public VecUnit getInitial_units() {
    long cPtr = bcJNI.PlanetMap_initial_units_get(swigCPtr, this);
    return (cPtr == 0) ? null : new VecUnit(cPtr, false);
  }

}
