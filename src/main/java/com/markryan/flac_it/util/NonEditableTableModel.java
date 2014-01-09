package com.markryan.flac_it.util;

/**
 * Created by markryan on 1/8/14.
 */
import javax.swing.table.DefaultTableModel;


public class NonEditableTableModel extends DefaultTableModel{

    /**
     * Extends the DefaultTableModel and makes the individual cells un-editable
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int rowIndex,int columnIndex){
        return false;
    }
}

