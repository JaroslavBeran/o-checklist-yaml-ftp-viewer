package jb.ochecklistviewer.ui;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;


public class DropHandler implements DropTargetListener {
    private static final Logger log = LoggerFactory.getLogger(DropHandler.class);
    private final ManualFileHandler manualFileHandler;

    @Setter
    private boolean enabled = true;


    DropHandler(ManualFileHandler manualFileHandler) {
        this.manualFileHandler = manualFileHandler;
    }


    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

        // Determine if we can actually process the contents coming in.
        // You could try and inspect the transferable as well, but
        // there is an issue on the MacOS under some circumstances
        // where it does not actually bundle the data until you accept the
        // drop.
        if (enabled && dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        } else {
            dtde.rejectDrag();
        }
    }


    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }


    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }


    @Override
    public void dragExit(DropTargetEvent dte) {
    }


    @SuppressWarnings("unchecked")
    @Override
    public void drop(DropTargetDropEvent dtde) {
        boolean success = false;

        // Is it what we require? ...
        if (enabled && dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Transferable transferable = dtde.getTransferable();
            try {
                List<File> data = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (data.size() == 1) {
                    manualFileHandler.handleFile(data.get(0));
                    success = true;
                }
            } catch (Exception exp) {
                log.warn("Cannot handle dropped file: " + exp.getMessage());
            }
        }

        if (success) {
            dtde.dropComplete(success);
        } else {
            dtde.rejectDrop();
        }
    }
}