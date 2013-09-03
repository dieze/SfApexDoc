package apex.com.main;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * SfApexDoc Eclipse plugin
 *
 * @author Steve Cox
 */
public class SfApexDocPlugin extends SfApexDoc implements IRunnableWithProgress {
  //---------------------------------------------------------------------------
  // Properties
  private IProgressMonitor monitor;

  
  //---------------------------------------------------------------------------
  // Methods
  /** Eclipse Plugin entry point */
  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    this.monitor = monitor;
    doIt();
    monitor.done();
  }
  
  public void initProgress(int units) {
    // progress (for each file: parse, write HTML)
    monitor.beginTask("SfApexDoc - documenting Apex Class files...", units);
  }
  
  public void showProgress(int units) {
    monitor.worked(1);
  }
}