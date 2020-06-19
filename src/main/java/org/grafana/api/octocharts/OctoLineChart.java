package org.grafana.api.octocharts;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.grafana.api.templates.Dashboard.GrafanaPanel.LineGraphLegendTpl;
import org.grafana.api.templates.Dashboard.GrafanaPanel.LineGraphPanelTpl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OctoLineChart extends OctoBaseChart {
    //static Logger log = Logger.getLogger(OctoLineChart.class.getName());
    static Logger log = Logger.getLogger("myLogger");
    private final String dashboarduid;
    private String dashboardtitle;
    private final String tableName;
    private String columns;
    private String timecolumn;
    private final String workunitClass;
    private final String workunitName;
    private final String wuRevision;
    public LineGraphPanelTpl lineGraph;

    public OctoLineChart(SparkSession spark, String dashboarduid, Dataset<Row> df, String workunitClass, String workunitName,String wuRevision, String summaryname){
        log.info("OctoLineChart being created with Dashboard UID: "+dashboarduid);
        this.dashboarduid = dashboarduid;
        this.dashboardtitle = null;
        this.lineGraph = new LineGraphPanelTpl();
        this.lineGraph.setDatasource(System.getenv("GRAFANA_POSTGRES_DATASOURCE"));
        this.lineGraph.setTitle(workunitName.substring(workunitName.lastIndexOf('.') + 1) + "_" + wuRevision + "_" + summaryname.substring(summaryname.lastIndexOf('.') + 1));
        this.lineGraph.setType("graph");
        LineGraphLegendTpl lgl = new LineGraphLegendTpl();
        lgl.setShow(true);
        this.lineGraph.setLegend(lgl);
        this.workunitClass = workunitClass;
        this.workunitName = workunitName;
        this.wuRevision = wuRevision;
        this.tableName=(workunitClass.substring(workunitClass.lastIndexOf('.') + 1) +"_"+ summaryname.substring(summaryname.lastIndexOf('.') + 1)).toLowerCase().replaceAll("[!@#$%^&*()--+={}:';|<>,.?/~` ]","_");
        this.updateChartData(spark,df,dashboarduid,workunitClass,workunitName,wuRevision,summaryname,tableName);
    }
    public void setColumns(String cols){
        this.columns = cols;
    }
    public void setTimeColumn(String col){
        this.timecolumn = col;
    }
    public void publish(){
        String query = String.format("SELECT\n  %s AS \"time\", %s FROM \"%s\" \nWHERE\n dashboardid = \'%s\' and workunitname = \'%s\' and wurevision = \'%s\' and $__timeFilter(%s)\nORDER BY 1",this.timecolumn,this.columns,this.tableName,this.dashboarduid,this.workunitName,this.wuRevision,this.timecolumn);
        this.lineGraph.setTargets(query,this.tableName,"time_series");
        try {
            super.publish(this.dashboarduid, this.dashboardtitle, this.lineGraph,this.workunitClass);
        }catch (Exception e){
            log.log(Level.SEVERE,"OctoLine Chart Publish Exception "+e.toString());
        }
    }
    public void setDashboardtitle(String dashboardtitle){
        this.dashboardtitle = dashboardtitle;
    }
}
