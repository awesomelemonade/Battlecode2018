/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package bc;

public class AsteroidStrike {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected AsteroidStrike(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(AsteroidStrike obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        bcJNI.delete_AsteroidStrike(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public AsteroidStrike(long karbonite, MapLocation location) {
    this(bcJNI.new_AsteroidStrike(karbonite, MapLocation.getCPtr(location), location), true);
  }

  public AsteroidStrike clone() {
    long cPtr = bcJNI.AsteroidStrike_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new AsteroidStrike(cPtr, false);
  }

  public String toString() {
    return bcJNI.AsteroidStrike_toString(swigCPtr, this);
  }

  public String toJson() {
    return bcJNI.AsteroidStrike_toJson(swigCPtr, this);
  }

  public boolean equals(AsteroidStrike other) {
    return bcJNI.AsteroidStrike_equals(swigCPtr, this, AsteroidStrike.getCPtr(other), other);
  }

  public void setKarbonite(long value) {
    bcJNI.AsteroidStrike_karbonite_set(swigCPtr, this, value);
  }

  public long getKarbonite() {
    return bcJNI.AsteroidStrike_karbonite_get(swigCPtr, this);
  }

  public void setLocation(MapLocation value) {
    bcJNI.AsteroidStrike_location_set(swigCPtr, this, MapLocation.getCPtr(value), value);
  }

  public MapLocation getLocation() {
    long cPtr = bcJNI.AsteroidStrike_location_get(swigCPtr, this);
    return (cPtr == 0) ? null : new MapLocation(cPtr, false);
  }

}
