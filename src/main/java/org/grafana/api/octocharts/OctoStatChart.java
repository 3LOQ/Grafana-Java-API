package org.grafana.api.octocharts;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.grafana.api.templates.Dashboard.GrafanaPanel.StatChartTpl;

public class OctoStatChart extends OctoBaseChart {
    private final String dashboarduid;
    private String dashboardtitle;
    private final String tableName;
    private String columns;
    private final String workunitClass;
    private final String workunitName;
    public StatChartTpl statpanel;

    public OctoStatChart(SparkSession spark, String dashboarduid, Dataset<Row> df, String workunitClass, String workunitName,String wuRevision, String summaryname){
        this.dashboarduid = dashboarduid;
        this.dashboardtitle = null;

        this.statpanel = new StatChartTpl();
        this.statpanel.setDatasource(System.getenv("POSTGRES_DATASOURCE"));
        this.statpanel.setTitle(workunitName.substring(workunitName.lastIndexOf('.') + 1) + "_" + wuRevision + "_" + summaryname.substring(summaryname.lastIndexOf('.') + 1));
        this.statpanel.setType("stat");
        this.workunitClass = workunitClass;
        this.workunitName = workunitName;
        this.tableName = (workunitClass.substring(workunitClass.lastIndexOf('.') + 1) + "_"+summaryname.substring(summaryname.lastIndexOf('.') + 1)).toLowerCase().replaceAll("[!@#$%^&*()--+={}:';|<>,.?/~` ]","_");
        this.updateChartData(spark,df,dashboarduid,workunitClass,workunitName,wuRevision,summaryname,this.tableName);

    }

    public void setColumns(String cols){
        this.columns = cols;
    }
    public void publish(){
        String query = String.format("SELECT\n %s FROM \"%s\" where dashboardid = \'%s\' and workunitname = \'%s\'",this.columns,this.tableName,this.dashboarduid, this.workunitName);
        this.statpanel.setTargets(query);
        super.publish(this.dashboarduid,this.dashboardtitle,this.statpanel,this.workunitClass);
    }
    public void setDashboardtitle(String dashboardtitle){
        this.dashboardtitle = dashboardtitle;
    }
}
