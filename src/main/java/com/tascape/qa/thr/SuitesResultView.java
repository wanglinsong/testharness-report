/*
 * Copyright 2015 - 2016 Nebula Bay.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tascape.qa.thr;

import com.tascape.qa.th.db.SuiteResult;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
@Named
@RequestScoped
public class SuitesResultView implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SuitesResultView.class);

    private static final long serialVersionUID = 1L;

    private String project = "";

    private long startTime = System.currentTimeMillis() - 5184000000L; // two months

    private long stopTime = System.currentTimeMillis() + 86400000L; // one day

    private int numberOfEntries = 100;

    private boolean invisibleIncluded = false;

    private String suiteName = "";

    private String jobName = "";

    @Inject
    private MySqlBaseBean db;

    private List<Map<String, Object>> results;

    private List<Map<String, Object>> resultsSelected;

    private BarChartModel barModel;

    private LineChartModel lineModel;

    @PostConstruct
    public void init() {
        this.getParameters();

        try {
            this.results = this.db.getSuitesResult(project, this.startTime, this.stopTime, this.numberOfEntries,
                this.suiteName, this.jobName, this.invisibleIncluded);
        } catch (NamingException | SQLException ex) {
            throw new RuntimeException(ex);
        }

        if (!this.suiteName.isEmpty() || !this.jobName.isEmpty()) {
//            this.barModel = this.initBarModel();
            this.lineModel = this.initLineModel();
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public boolean isInvisibleIncluded() {
        return invisibleIncluded;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<Map<String, Object>> getResults() {
        return results;
    }

    public List<Map<String, Object>> getResultsSelected() {
        return resultsSelected;
    }

    public void setResultsSelected(List<Map<String, Object>> resultsSelected) {
        this.resultsSelected = resultsSelected;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public void setNumberOfEntries(int numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public void setInvisibleIncluded(boolean invisibleIncluded) {
        this.invisibleIncluded = invisibleIncluded;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public String getProject() {
        return project;
    }

    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        model.setLegendPosition("n");
        model.setLegendPlacement(LegendPlacement.OUTSIDEGRID);
        model.setLegendCols(3);
        model.setSeriesColors("00ff00, ff0000");
        model.setStacked(true);
        model.setAnimate(false);
        model.setShowPointLabels(true);
        model.setZoom(true);

        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Number of Tests");
        model.getAxis(AxisType.X).setTickAngle(-90);

        ChartSeries pass = new BarChartSeries();
        pass.setLabel("PASS");
        model.addSeries(pass);
        ChartSeries fail = new BarChartSeries();
        fail.setLabel("FAIL");
        model.addSeries(fail);

        int index = 0;
        for (Map<String, Object> result : this.results) {
            int f = (int) result.get(SuiteResult.NUMBER_OF_FAILURE);
            int t = (int) result.get(SuiteResult.NUMBER_OF_TESTS);
            float s = ((long) result.get(SuiteResult.STOP_TIME) - (long) result.get(SuiteResult.START_TIME)) / 1000.0f;
            String x = (++index) + "";
            fail.set(x, f);
            pass.set(x, t - f);
        }
        return model;
    }

    private LineChartModel initLineModel() {
        LineChartModel model = new LineChartModel();
        model.setLegendPosition("n");
        model.setLegendPlacement(LegendPlacement.OUTSIDEGRID);
        model.setLegendCols(3);
        model.setSeriesColors("000000, ff0000, 00ff00");
        model.setAnimate(true);
        model.setShowPointLabels(false);
        model.setZoom(true);
        model.setBreakOnNull(true);

        Axis xAxis = new CategoryAxis();
        xAxis.setTickAngle(-90);
        model.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Number of Tests");
        yAxis.setMin(0);

        Axis y2Axis = new LinearAxis("Execution Time (second)");
        y2Axis.setMin(0);
        model.getAxes().put(AxisType.Y2, y2Axis);

        ChartSeries time = new LineChartSeries();
        time.setLabel("EXECUTION TIME");
        time.setXaxis(AxisType.X);
        time.setYaxis(AxisType.Y2);
        model.addSeries(time);

        ChartSeries fail = new LineChartSeries();
        fail.setLabel("FAIL");
        model.addSeries(fail);
        ChartSeries total = new LineChartSeries();
        total.setLabel("TOTAL");
        model.addSeries(total);

        int index = 0;
        for (Map<String, Object> result : this.results) {
            int f = (int) result.get(SuiteResult.NUMBER_OF_FAILURE);
            int t = (int) result.get(SuiteResult.NUMBER_OF_TESTS);
            float s = ((long) result.get(SuiteResult.STOP_TIME) - (long) result.get(SuiteResult.START_TIME)) / 1000.0f;
            String x = (++index) + "";
            time.set(x, s);
            fail.set(x, f == 0 ? null : f);
            total.set(x, t == f ? null : t);
        }
        return model;
    }

    private void getParameters() {
        Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String v = map.get("start");
        if (v != null) {
            this.startTime = Long.parseLong(v);
            LOG.debug("start={}", this.startTime);
        }
        v = map.get("stop");
        if (v != null) {
            this.stopTime = Long.parseLong(v);
            LOG.debug("stop={}", this.stopTime);
        }
        v = map.get("number");
        if (v != null) {
            this.numberOfEntries = Integer.parseInt(v);
            LOG.debug("number={}", this.numberOfEntries);
        }
        v = map.get("invisible");
        if (v != null) {
            this.invisibleIncluded = Boolean.parseBoolean(v);
            LOG.debug("invisible={}", this.invisibleIncluded);
        }
        v = map.get("suite");
        if (v != null) {
            this.suiteName = v;
            LOG.debug("suite={}", this.suiteName);
        }
        v = map.get("job");
        if (v != null) {
            this.jobName = v;
            LOG.debug("job={}", this.jobName);
        }
        v = map.get("project");
        if (v != null) {
            this.project = v;
            LOG.debug("project={}", this.project);
        }
    }
}
