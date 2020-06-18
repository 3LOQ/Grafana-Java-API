package org.grafana.api.octocharts;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.SparkSession;

import org.grafana.api.GrafanaAPI;
import org.grafana.api.templates.Dashboard.DashboardTpl;
import org.grafana.api.responses.Dashboard.DashboardRsp;
import org.grafana.api.templates.Dashboard.CreateUpdateDashboardTpl;
import org.grafana.api.responses.Dashboard.NewCreateUpdateDashboardRsp;
import org.grafana.api.templates.Dashboard.abstractbasepanel.BasePanelTpl;

import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Logger;
import java.time.LocalDateTime;

public abstract class OctoBaseChart {
    static Logger log = Logger.getLogger("myLogger");
    public void updateChartData(SparkSession spark,Dataset<Row> df,String dashboarduid,String workunitClass, String workunitname,String wuRevision,String summaryname,String tableName){
        log.info("UpdateChartData "+" Spark Session Id: " + spark +" Table Name: " + tableName);
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", System.getenv("GRAFANA_POSTGRES_USERNAME"));
        connectionProperties.put("password", System.getenv("GRAFANA_POSTGRES_PASSWORD"));
        connectionProperties.put("driver", "org.postgresql.Driver");

        df = df.withColumn("dashboardid",functions.lit(dashboarduid));
        df = df.withColumn("workunitclass", functions.lit(workunitClass));
        df = df.withColumn("workunitname", functions.lit(workunitname));
        df = df.withColumn("wurevision", functions.lit(wuRevision));
        df = df.withColumn("summaryname",functions.lit(summaryname));
        df.write()
                .mode("append") //table level { ignore,append,overwrite }
                .jdbc("jdbc:postgresql://"+System.getenv("GRAFANA_POSTGRES_URL")+"/"+System.getenv("GRAFANA_POSTGRES_DB"), tableName, connectionProperties);
    }

    public void publish(String uid, String dashboardtitle, BasePanelTpl panel,String workunitName){
        log.info("Method Name : BasePanelTpl Publish"+"Uid : "+uid + "Dashboard Title : "+dashboardtitle);

        String grafanaserver = System.getenv("GRAFANA_SERVER");
        String grafana_username = System.getenv("GRAFANA_USERNAME");
        String grafana_password = System.getenv("GRAFANA_PASSWORD");
        GrafanaAPI grafanaAPI = new GrafanaAPI(grafanaserver);
        DashboardTpl dashItems;
        DashboardRsp dashboardRsp = grafanaAPI.orgAdminAPI(grafana_username,grafana_password).getDashboardByUid(uid);
        if (dashboardRsp == null){
            dashItems = new DashboardTpl();
            dashItems.setUid(uid);
            if (dashboardtitle == null) {
                LocalDateTime localDt= LocalDateTime.now();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
                String formattedDateTime = localDt.format(format);
                dashItems.setTitle(uid +"_"+formattedDateTime);
            }else{
                dashItems.setTitle(dashboardtitle);
            }
        }else{
            dashItems = dashboardRsp.getDashboard();
        }
        CreateUpdateDashboardTpl dashTest = new CreateUpdateDashboardTpl();
        dashItems.setPanels(panel);
        dashTest.setDashboard(dashItems);
        NewCreateUpdateDashboardRsp createUpdateDashboard = grafanaAPI.orgAdminAPI(grafana_username,grafana_password).createUpdateDashboard(dashTest);
        log.info("success");
    }
}
