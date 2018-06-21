package com.graly.erp.inv.transfer.to.cana;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableViewTest {

    public static void main(String[] args) {
       
        Display exampleDisplay = new Display();
        Shell exampleShell = new Shell(exampleDisplay);

        exampleShell.setBounds(120, 120, 345, 220);
        exampleShell.setLayout(new FillLayout());

        final TableViewer myTableViewer = new TableViewer(exampleShell,
                SWT.SINGLE);

        final Table myTable = myTableViewer.getTable();

        String[] myColumns = new String[] { "Hello", "Bye" };

        for (int i = 0; i < myColumns.length; i++) {
            TableColumn tableColumn = new TableColumn(myTable, SWT.NONE);
            tableColumn.setText(myColumns[i]);
            tableColumn.setWidth(100);
        }

        myTableViewer.setLabelProvider(new PersonTableLabelProvider());

        myTableViewer.setContentProvider(new ArrayContentProvider());

        myTableViewer.setInput(Example.getInput());
       
        myTable.setHeaderVisible(true);

        exampleShell.open();

        while (!exampleShell.isDisposed()) {
            if (!exampleDisplay.readAndDispatch())
                exampleDisplay.sleep();
        }

        exampleDisplay.dispose();
    }
}

class PersonTableLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    public Image getColumnImage(Object element, int index) {
        return null;
    }

    public String getColumnText(Object element, int index) {
        Example ex = (Example) element;
        switch (index) {
        case 0:
            return ex.hello;
        case 1:
            return ex.bye;
        }

        return null;
    }
}

class Example {
    String hello;

    String bye;

    Example(String hello, String bye) {
        this.hello = hello;
        this.bye = bye;
    }

    public static Example[] getInput() {
        return new Example[] { new Example("FirstHello", "FirstBye"),
                new Example("SecondHello", "SecondBye") };
    }
}