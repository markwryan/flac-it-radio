package com.markryan.flac_it.util;

/**
 * Created by markryan on 1/8/14.
 */
import javax.swing.table.DefaultTableModel;


public class NonEditableTableModel extends DefaultTableModel{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int rowIndex,int columnIndex){
        return false;
    }
}

