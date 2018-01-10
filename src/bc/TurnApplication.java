/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package bc;

public class TurnApplication {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected TurnApplication(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(TurnApplication obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        bcJNI.delete_TurnApplication(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public TurnApplication() {
    this(bcJNI.new_TurnApplication(), true);
  }

  public void setStart_turn(StartTurnMessage value) {
    bcJNI.TurnApplication_start_turn_set(swigCPtr, this, StartTurnMessage.getCPtr(value), value);
  }

  public StartTurnMessage getStart_turn() {
    long cPtr = bcJNI.TurnApplication_start_turn_get(swigCPtr, this);
    return (cPtr == 0) ? null : new StartTurnMessage(cPtr, false);
  }

  public void setViewer(ViewerMessage value) {
    bcJNI.TurnApplication_viewer_set(swigCPtr, this, ViewerMessage.getCPtr(value), value);
  }

  public ViewerMessage getViewer() {
    long cPtr = bcJNI.TurnApplication_viewer_get(swigCPtr, this);
    return (cPtr == 0) ? null : new ViewerMessage(cPtr, false);
  }

}
