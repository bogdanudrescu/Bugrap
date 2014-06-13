package com.example.bugrap.report;

import java.util.Date;
import java.util.Set;

import org.vaadin.bugrap.domain.BugrapRepository.ReportsQuery;
import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.data.DataManager;
import com.example.utils.Utils;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * The reports table view component.
 * 
 * @author bogdanudrescu
 */
@SuppressWarnings("serial")
public class ReportTable extends Table {

	/*
	 * The reports data source.
	 */
	private Container reportsDataSource = new BeanItemContainer<Report>(Report.class);

	/**
	 * Create the reports table.
	 */
	public ReportTable() {
		setSizeFull();
		setSelectable(true);
		setMultiSelect(true);

		setContainerDataSource(reportsDataSource);
		setDefaultVisibleColumns();

		setColumnHeader("version", "Version");
		setColumnHeader("priority", "Priority");
		setColumnHeader("type", "Type");
		setColumnHeader("summary", "Summary");
		setColumnHeader("assigned", "Assigned to");
		setColumnHeader("timestamp", "Last modified");
		setColumnHeader("reportedTimestamp", "Reported");

		// Date cell custom renderer.
		DateIntervalCell dateIntervalCell = new DateIntervalCell();
		addGeneratedColumn("timestamp", dateIntervalCell);
		addGeneratedColumn("reportedTimestamp", dateIntervalCell);
	}

	/*
	 * Sets the default visible column.
	 */
	private void setDefaultVisibleColumns() {
		setVisibleColumns("priority", "type", "summary", "assigned", "timestamp", "reportedTimestamp");
	}

	/*
	 * Sets all versions visible column.
	 */
	private void setAllVersionsVisibleColumns() {
		setVisibleColumns("version", "priority", "type", "summary", "assigned", "timestamp", "reportedTimestamp");
	}

	/*
	 * Generate the cell value for the date columns, showing how much time passed since the specified date.
	 */
	private class DateIntervalCell implements ColumnGenerator {

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Table.ColumnGenerator#generateCell(com.vaadin.ui.Table, java.lang.Object, java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Object generateCell(Table source, Object itemId, Object columnId) {
			Property<Date> property = source.getContainerProperty(itemId, columnId);

			if (property != null && property.getType().equals(Date.class)) {
				return new Label(Utils.stringIntervalFromDateUntilNow(property.getValue()));
			}

			return null;
		}

	}

	/**
	 * Refresh the reports according to the specified query.
	 * @param query	the query to call on the table data source.
	 */
	public void refreshReports(ReportsQuery query) {
		reportsDataSource.removeAllItems();

		Set<Report> reports = DataManager.getBugrapRepository().findReports(query);
		for (Report report : reports) {
			reportsDataSource.addItem(report);
		}

		System.out.println("refreshReports for " + query + " output " + reports.size() + " results");
	}

	/**
	 * Sets the visible state of the version column.
	 * @param versionVisible	the visible state of the version column.
	 */
	public void setVersionColumnVisible(boolean versionVisible) {
		if (versionVisible) {
			setAllVersionsVisibleColumns();
		} else {
			setDefaultVisibleColumns();
		}
	}

}
