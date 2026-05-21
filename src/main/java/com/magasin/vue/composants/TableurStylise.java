package com.magasin.vue.composants;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/** JTable preconfiguree pour un rendu propre. */
public class TableurStylise extends JTable {

    public TableurStylise(TableModel modele) {
        super(modele);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(34);
        setShowGrid(false);
        setIntercellSpacing(new java.awt.Dimension(0, 0));
        setFillsViewportHeight(true);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setGridColor(new Color(0xE5E7EB));

        JTableHeader entete = getTableHeader();
        entete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        entete.setBackground(new Color(0xF3F4F6));
        entete.setForeground(new Color(0x374151));
        entete.setPreferredSize(new java.awt.Dimension(0, 38));
        entete.setReorderingAllowed(false);

        setDefaultRenderer(Object.class, new RenduAlterne());
    }

    private static class RenduAlterne extends DefaultTableCellRenderer {
        private static final Color CLAIR = Color.WHITE;
        private static final Color FONCE = new Color(0xF9FAFB);
        private static final Color SELECTION = new Color(0xE0E7FF);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                c.setBackground(SELECTION);
                c.setForeground(new Color(0x1E40AF));
            } else {
                c.setBackground(row % 2 == 0 ? CLAIR : FONCE);
                c.setForeground(new Color(0x1F2937));
            }
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return c;
        }
    }
}
