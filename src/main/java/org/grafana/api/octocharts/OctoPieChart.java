package org.grafana.api.octocharts;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.grafana.api.templates.Dashboard.GrafanaPanel.PieChartPanelTpl;

public class OctoPieChart extends OctoBaseChart {
    private String dashboarduid;
    private String dashboardtitle;
    private String tableName;
    private String columns;
    private String workunitClass;
    private String workunitName;
    public PieChartPanelTpl piepanel;

    public OctoPieChart(SparkSession spark, String dashboarduid, Dataset<Row> df, String workunitClass, String workunitname,String wuRevision, String summaryname){
        this.dashboarduid = dashboarduid;
        this.piepanel = new PieChartPanelTpl();
        this.piepanel.setDatasource(System.getenv("GRAFANA_POSTGRES_DATASOURCE"));
        this.piepanel.setTitle(workunitName.substring(workunitName.lastIndexOf('.') + 1) + "_" + wuRevision + "_" + summaryname.substring(summaryname.lastIndexOf('.') + 1));
        this.piepanel.setPieType("pie");
        this.workunitClass=workunitClass;
        this.workunitName=workunitname;
        this.tableName=(workunitClass.substring(workunitClass.lastIndexOf('.') + 1) +"_"+ summaryname.substring(summaryname.lastIndexOf('.') + 1)).toLowerCase().replaceAll("[!@#$%^&*()--+={}:';|<>,.?/~` ]","_");
        this.updateChartData(spark,df,dashboarduid,workunitClass,workunitname,wuRevision,summaryname,tableName);
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }
    public void setPietype(String type){
        this.piepanel.setPieType(type);
    }

    public void publish(){
        String query = String.format("SELECT\n now() as time, %s FROM \"%s\" where dashboardid = \'%s\' and workunitname = \'%s\'",this.columns,this.tableName,this.dashboarduid, this.workunitName);
        this.piepanel.setTargets(query,"time_series");
        super.publish(this.dashboarduid,this.dashboardtitle,this.piepanel,this.workunitClass);
    }
    public void setDashboardtitle(String dashboardtitle){
        this.dashboardtitle = dashboardtitle;
    }
}
