package de.thb.webbaki.service.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportComparison {
    private int quarter;
    private int year;
    private int[] quarters;
    private int[] years;

    private MappingReport companyReport;
    private MappingReport[] companyReports;

    private MappingReport branchReport;
    private MappingReport[] branchReports;

    private MappingReport sectorReport;
    private MappingReport[] sectorReports;

    private MappingReport nationalReport;
    private MappingReport[] nationalReports;

    public ReportComparison(){
        quarters = new int[4];
        years = new int[4];

        companyReports = new MappingReport[4];
        branchReports = new MappingReport[4];
        sectorReports = new MappingReport[4];
        nationalReports = new MappingReport[4];
    }

    public void addNewQuarter(int quarter){
        for(int i = 0; i < quarters.length; i++){
            if(quarters[i] == 0){
                quarters[i] = quarter;
                break;
            }
        }
    }

    public void addNewYear(int year){
        for(int i = 0; i < years.length; i++){
            if(years[i] == 0){
                years[i] = year;
                break;
            }
        }
    }

    public void addNewCompanyReport(MappingReport report){
        for(int i = 0; i < companyReports.length; i++){
            if(companyReports[i] == null){
                companyReports[i] = report;
                break;
            }
        }
    }

    public void addNewBranchReport(MappingReport report){
        for(int i = 0; i < branchReports.length; i++){
            if(branchReports[i] == null){
                branchReports[i] = report;
                break;
            }
        }
    }

    public void addNewSectorReport(MappingReport report){
        for(int i = 0; i < sectorReports.length; i++){
            if(sectorReports[i] == null){
                sectorReports[i] = report;
                break;
            }
        }
    }

    public void addNewNationalReport(MappingReport report){
        for(int i = 0; i < nationalReports.length; i++){
            if(nationalReports[i] == null){
                nationalReports[i] = report;
                break;
            }
        }
    }

}
