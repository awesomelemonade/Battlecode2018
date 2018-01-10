/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package bc;

public class VecMapLocation {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected VecMapLocation(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(VecMapLocation obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        bcJNI.delete_VecMapLocation(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public VecMapLocation() {
    this(bcJNI.new_VecMapLocation(), true);
  }

  public String toString() {
    return bcJNI.VecMapLocation_toString(swigCPtr, this);
  }

  public VecMapLocation clone() {
    long cPtr = bcJNI.VecMapLocation_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new VecMapLocation(cPtr, false);
  }

  public long size() {
    return bcJNI.VecMapLocation_size(swigCPtr, this);
  }

  public MapLocation get(long index) {
    long cPtr = bcJNI.VecMapLocation_get(swigCPtr, this, index);
    return (cPtr == 0) ? null : new MapLocation(cPtr, false);
  }

}
