package org.grafana.api.octocharts;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.grafana.api.templates.Dashboard.GrafanaPanel.TablePanelTpl;

public class OctoTableChart extends OctoBaseChart {
    private final String dashboarduid;
    private String dashboardtitle;
    private final String tableName;
    private String columns;
    private final String workunitClass;
    private final String workunitName;
    private final String wuRevision;
    public TablePanelTpl tablepanel;

    public OctoTableChart(SparkSession spark, String dashboarduid, Dataset<Row> df, String workunitClass, String workunitName,String wuRevision, String summaryname){
        this.dashboarduid = dashboarduid;
        this.dashboardtitle = null;
        this.tablepanel = new TablePanelTpl();
        this.tablepanel.setDatasource(System.getenv("GRAFANA_POSTGRES_DATASOURCE"));
        this.tablepanel.setTitle(workunitName.substring(workunitName.lastIndexOf('.') + 1) + "_" + wuRevision + "_" + summaryname.substring(summaryname.lastIndexOf('.') + 1));
        this.tablepanel.setType("table");
        this.workunitClass = workunitClass;
        this.workunitName = workunitName;
        this.wuRevision = wuRevision;
        this.tableName=(workunitClass.substring(workunitClass.lastIndexOf('.') + 1) +"_"+ summaryname.substring(summaryname.lastIndexOf('.') + 1)).toLowerCase().replaceAll("[!@#$%^&*()--+={}:';|<>,.?/~` ]","_");
        this.updateChartData(spark,df,dashboarduid,workunitClass,workunitName,wuRevision,summaryname,this.tableName);
    }
    public void setColumns(String cols){
        this.columns = cols;
    }
    public void publish(){
        String query = String.format("SELECT\n %s FROM \"%s\" where dashboardid = \'%s\' and workunitname = \'%s\' and wurevision = \'%s\'",this.columns,this.tableName,this.dashboarduid, this.workunitName,this.wuRevision);
        this.tablepanel.setTargets(query);
        super.publish(this.dashboarduid,this.dashboardtitle,this.tablepanel,this.workunitClass);
    }
    public void setDashboardtitle(String dashboardtitle){
        this.dashboardtitle = dashboardtitle;
    }
}
