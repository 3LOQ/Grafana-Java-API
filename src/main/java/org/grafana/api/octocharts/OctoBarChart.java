package org.grafana.api.octocharts;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.grafana.api.templates.Charts.PlotlyPanelChart;
import java.util.logging.Level;
import java.util.logging.Logger;
public class OctoBarChart extends OctoBaseChart{
    //static Logger log = Logger.getLogger(OctoBarChart.class.getName());
    static Logger log = Logger.getLogger("myLogger");
    private final String uid;
    private String dashboardTitle;
    private final String tableName;
    private final String workunitClass;
    private final String workunitName;
    public PlotlyPanelChart barpanel;

    public OctoBarChart(SparkSession spark,String dashboarduid, Dataset<Row> df,String workunitClass,String workunitName,String wuRevision, String summaryname, String xtitle, String ytitle){

        log.info("Bar chart being created with dashboardid :" + dashboarduid);

        this.uid = dashboarduid;
        this.dashboardTitle = null;
        this.barpanel = new PlotlyPanelChart();
        this.barpanel.setDatasource(System.getenv("GRAFANA_POSTGRES_DATASOURCE"));
        this.barpanel.setPconfig(xtitle,ytitle,"bar");
        this.barpanel.setTitle(workunitName.substring(workunitName.lastIndexOf('.') + 1) + "_" + wuRevision + "_" + summaryname.substring(summaryname.lastIndexOf('.') + 1));
        this.workunitClass = workunitClass;
        this.workunitName = workunitName;
        this.tableName=(workunitClass.substring(workunitClass.lastIndexOf('.') + 1) +"_"+ summaryname.substring(summaryname.lastIndexOf('.') + 1)).toLowerCase().replaceAll("[!@#$%^&*()--+={}:';|<>,.?/~` ]","_");
        this.updateChartData(spark,df,dashboarduid,workunitClass,workunitName,wuRevision,summaryname,this.tableName);
    }
    public void setTrace(String xmapping,String ymapping){
        log.info("X Mapping :"+xmapping+" Y Mapping : "+ymapping);
        this.barpanel.setTraces(xmapping,ymapping);
        this.barpanel.setTargets(String.format("select %s,%s from \"%s\" where dashboardid = \'%s\' and workunitname = \'%s\'",xmapping,ymapping,this.tableName,this.uid, this.workunitName));
    }
    public void setTarget(String query){
        log.info("Set Target ");
        this.barpanel.setTargets(query);
    }


    public void setDashboardtitle(String dashboardtitle){
        this.dashboardTitle = dashboardtitle;
        log.info("Set Dashboard Title: "+ dashboardtitle);
    }
    public void publish(){

        log.info("OctoBar Chart Publish");
        try{
        super.publish(this.uid,this.dashboardTitle,this.barpanel,this.workunitClass);
    }catch (Exception e){
            log.log(Level.SEVERE,"Excepion "+e);
        }

    }

}
