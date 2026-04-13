package server.classes;

/**
 * YearlyReportClass
 * Handles the business logic for Yearly Leave Report Generation (RYAN).
 * Integrates with ReportDAO safely to prevent raw SQL access from business layers.
 */
public class YearlyReportClass {

    private static final ReportDAO reportDAO = new ReportDAO();

    /**
     * Generates a nicely formatted Yearly Leave Report for an individual user.
     * 
     * @param userId The ID of the user requesting the report
     * @param year   The year for the report
     * @return String output of the formatted report
     */
    public static String generateIndividualReport(int userId, int year) {
        
        if (year < 2000 || year > 2100) {
            return "❌ Report Generation Failed: Invalid Year provided.";
        }

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("==================================================\n");
        reportBuilder.append("         YEARLY LEAVE REPORT (INDIVIDUAL)         \n");
        reportBuilder.append("                   YEAR: ").append(year).append("\n");
        reportBuilder.append("==================================================\n\n");
        
        // Fetch DB data via DAO
        String reportData = reportDAO.fetchIndividualLeaveData(userId, year);
        reportBuilder.append(reportData).append("\n");
        
        reportBuilder.append("==================================================\n");
        reportBuilder.append("             END OF INDIVIDUAL REPORT             \n");
        reportBuilder.append("==================================================\n");

        return reportBuilder.toString();
    }

    /**
     * Generates a nicely formatted Yearly Leave Report for the entire company.
     * Accessible by HR Managers to see everyone's data.
     * 
     * @param year   The year for the report
     * @return String output of the formatted report
     */
    public static String generateCompanyReport(int year) {
        
        if (year < 2000 || year > 2100) {
            return "❌ Report Generation Failed: Invalid Year provided.";
        }

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("==================================================\n");
        reportBuilder.append("         YEARLY LEAVE REPORT (COMPANY-WIDE)       \n");
        reportBuilder.append("                   YEAR: ").append(year).append("\n");
        reportBuilder.append("==================================================\n\n");
        
        // Fetch DB data via DAO
        String reportData = reportDAO.fetchCompanyLeaveData(year);
        reportBuilder.append(reportData).append("\n");
        
        reportBuilder.append("==================================================\n");
        reportBuilder.append("              END OF COMPANY REPORT               \n");
        reportBuilder.append("==================================================\n");

        return reportBuilder.toString();
    }
}
