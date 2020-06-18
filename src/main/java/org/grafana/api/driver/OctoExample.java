package org.grafana.api.driver;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.grafana.api.octocharts.*;

public class OctoExample {
    public static void main(String[] args){
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("Java Spark SQL basic example")
                .config("spark.some.config.option", "some-value")
                .getOrCreate();

        Dataset<Row> df1 = spark.read().format("csv").option("header","true").load("D:/temp/heatmap_data/sample1.csv");

        Dataset<Row> df2 = spark.read().format("csv").option("header","true").load("D:/temp/line_chart_data/cards_activation.csv");

        OctoBarChart octoBarChart = new OctoBarChart(spark,"ABCDM",df1,"asd.abcd.worker", "adsd.abc.Sampleworkunit","01","abcd.Sample Summary-2","xdata","ydata");
        octoBarChart.setTrace("employees","jan");
        octoBarChart.publish();

        OctoLineChart octoLineChart = new OctoLineChart(spark,"ABCDM",df2,"asd.abcd.worker","Lineworkunit","01","Line_summary(tumri)");
        octoLineChart.setTimeColumn("year_month");
        octoLineChart.setColumns("activation_percentage_30_days,activation_percentage_60_days,activation_percentage_90_days");
        octoLineChart.publish();

        OctoHeatmapChart octoHeatmapChart = new OctoHeatmapChart(spark,"ABCDM",df1,"abcd.sampleworkunit_2", "Heatworkunit","01","HeatSummary,","xdata","ydata");
        octoHeatmapChart.setXaxis("jan,feb,mar"); //Always a string containing either (one column header) or  (List of column headers)
        octoHeatmapChart.setYaxis("employees");
        octoHeatmapChart.publish();

        OctoTableChart octoTableChart = new OctoTableChart(spark,"ABCDM",df1, "abcd.sampleworkunit_2", "Tableworkunit","01","TableSummary:");
        octoTableChart.setColumns("employees,jan");
        octoTableChart.publish();

        spark.stop();

    }
}
