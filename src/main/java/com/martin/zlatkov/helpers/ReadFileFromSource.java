package com.martin.zlatkov.helpers;

import com.martin.zlatkov.models.Employee;
import com.martin.zlatkov.models.FileDB;
import com.martin.zlatkov.models.Pair;
import com.martin.zlatkov.models.WorkTogether;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ReadFileFromSource {

    public List<Employee> readFile(FileDB fileDB) throws IOException {
        List<Employee> allEmps = new ArrayList<>();
        ReadFileFromSource obj = new ReadFileFromSource();
        //InputStream inputStream = obj.getClass().getClassLoader().getResourceAsStream(fileNane);

        //boolean check = false;
        //new InputStreamReader(inputStream)
        byte[] blobFile = fileDB.getData();
        InputStream is = new ByteArrayInputStream(blobFile);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            String[] ar;
            while ((line = reader.readLine()) != null) {
                ar = line.split(",");
                Employee emp = new Employee();
                emp.setID1(Integer.parseInt(ar[0].trim()));
                emp.setProjectID(Integer.parseInt(ar[1].trim()));
                emp.setStartDate(determineDateFormat(ar[2].trim()));
                if (ar[3].trim().equals("NULL")) {
                    //check=true;
                    //if(check) {
                    String pattern = determineDateFormatPattern(ar[2].trim());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
                    LocalDate ld = LocalDate.now();
                    String nowD = ld.format(dtf);
                    Date d = new SimpleDateFormat(pattern).parse(nowD);
                    emp.setEdnDate(d);
                    // }
                } else {
                    emp.setEdnDate(determineDateFormat(ar[3].trim()));
                }
                allEmps.add(emp);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return allEmps;
    }

    public List<WorkTogether> calculatePair(List<Employee> allEmployees) {
        //Grouping the employees according project id, where they have been working for some days
        //Employees per Project as List
        //List<EmployeeReportingTest> empGroupPerProjctID = allEmployees.stream().sorted((o1, o2) -> o1.getProjectID()).collect(Collectors.toList());
        Map<Integer, List<Employee>> empP = allEmployees.stream().collect(Collectors.groupingBy(Employee::getProjectID));

        //Final results all work on same project
        List<WorkTogether> allWorkTogether = new ArrayList<>();

        for (Map.Entry<Integer, List<Employee>> entry : empP.entrySet()) {
            List<Employee> empPerProject = entry.getValue();

            //System.out.println(empPerProject.size());

            for (int i = 0; i < empPerProject.size(); i++) {
                List<Employee> empList1 = new ArrayList<>();
                empList1.add(entry.getValue().get(i));

                for (Employee e : empList1) {
                    Date startDate1 = e.getStartDate();
                    Date endDate1 = e.getEdnDate();

                    for (int j = i + 1; j < empPerProject.size(); j++) {
                        List<Employee> empList2 = new ArrayList<>();
                        empList2.add(entry.getValue().get(j));

                        for (Employee ee : empList2) {
                            Date startDate2 = ee.getStartDate();
                            Date endDate2 = ee.getEdnDate();

                            if (startDate1.after(endDate1) || endDate1.before(startDate2)) {
                                continue;
                            }

                            long daysWorkingTogether = calculateDaysWorkingTogether(startDate1, endDate1, startDate2, endDate2);

                            if (daysWorkingTogether > 0) {
                                allWorkTogether.add(new WorkTogether(e.getID1(), ee.getID1(), e.getProjectID(), (int) daysWorkingTogether));
                            }
                        }
                    }
                }
            }
        }
        List<WorkTogether> pairsWorkedTogether = new ArrayList<>();

        for (int i = 0; i < allWorkTogether.size(); i++) {
            String s1 = String.valueOf(allWorkTogether.get(i).getID1());
            String s2 = String.valueOf(allWorkTogether.get(i).getID2());
            int s3 = allWorkTogether.get(i).getDaysTotalTogether();
            String both = s1 + s2;
            for (int j = i + 1; j < allWorkTogether.size(); j++) {
                String k1 = String.valueOf(allWorkTogether.get(j).getID1());
                String k2 = String.valueOf(allWorkTogether.get(j).getID2());
                int k3 = allWorkTogether.get(j).getDaysTotalTogether();
                String bothNew = k1 + k2;
                if (both.equals(bothNew)) {
                    int daysTogether = s3 + k3;
                    pairsWorkedTogether.add(new WorkTogether(allWorkTogether.get(j).getID1(), allWorkTogether.get(j).getID2(), allWorkTogether.get(j).getProjectID(), daysTogether));
                }
            }
        }

        List<WorkTogether> workLikeEmployeePairOnProjects = pairsWorkedTogether;
        workLikeEmployeePairOnProjects.stream().collect(Collectors.groupingBy(WorkTogether::getDaysTotalTogether));
        List<Employee> t = new ArrayList<>();
        return workLikeEmployeePairOnProjects;
    }

    /**
     * Helper Methods for Dates Patterns
     *
     * @param dateToGuess String date to be checked if matches some specific date pattern
     */
    public static Date determineDateFormat(String dateToGuess) {
        // .replace("/", "").replace("-", "").replace(" ", "");
        String tempDate = dateToGuess;
        String dateFormat = "";

        if (dateToGuess.contains("/")) {
            if (tempDate.matches("^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$")) {
                dateFormat = "MM/dd/yyyy";
            } else if (tempDate.matches("([0-31]{2})/([0-12]{2})/([0-9]{4})")) {
                dateFormat = "dd/MM/yyyy";
            } else if (tempDate.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
                dateFormat = "yyyy/MM/dd";
            } else if (tempDate.matches("^\\d{4}.\\d{1,2}.\\d{1,2}$")) {
                dateFormat = "yyyy/MM/dd";
            } else if (tempDate.matches("([a-z]{3})/([0-31]{2})/([0-9]{4})")) {
                dateFormat = "MMM/dd/yyyy";
            } else if (tempDate.matches("([0-9]{4})/([a-z]{3})/([0-31]{2})")) {
                dateFormat = "yyyy/MMM/dd";
            }
        } else if (dateToGuess.contains("-")) {
            if (tempDate.matches("^[0-3]?[0-9]-[0-3]?[0-9]-(?:[0-9]{2})?[0-9]{2}$")) {
                dateFormat = "MM-dd-yyyy";
            } else if (tempDate.matches("([0-31]{2})-([0-12]{2})-([0-9]{4})")) {
                dateFormat = "dd-MM-yyyy";
            } else if (tempDate.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                dateFormat = "yyyy-MM-dd";
            } else if (tempDate.matches("([a-z]{3})-([0-31]{2})-([0-9]{4})")) {
                dateFormat = "MMM-dd-yyyy";
            } else if (tempDate.matches("([0-9]{4})-([a-z]{3})-([0-31]{2})")) {
                dateFormat = "yyyy-MMM-dd";
            }
        } else if (dateToGuess.contains(".")) {
            if (tempDate.matches("^[0-3]?[0-9].[0-3]?[0-9].(?:[0-9]{2})?[0-9]{2}$")) {
                dateFormat = "MM.dd.yyyy";
            } else if (tempDate.matches("([0-31]{2}).([0-12]{2}).([0-9]{4})")) {
                dateFormat = "dd.MM.yyyy";
            } else if (tempDate.matches("([a-z]{3}).([0-31]{2}).([0-9]{4})")) {
                dateFormat = "MMM.dd.yyyy";
            } else if (tempDate.matches("([0-9]{4}).([a-z]{3}).([0-31]{2})")) {
                dateFormat = "yyyy.MMM.dd";
            }
        } else {
            if (tempDate.matches("([0-9]{4})([0-31]{2})([0-12]{2})")) {
                dateFormat = "yyyyddMM";
            } else if (tempDate.matches("([0-31]{2})([a-z]{3})([0-9]{4})")) {
                dateFormat = "ddMMMyyyy";
            } else if (tempDate.matches("([0-9]{4})([a-z]{3})([0-31]{2})")) {
                dateFormat = "yyyyMMMdd";
            } else if (tempDate.matches("([0-9]{4})([0-31]{2})([a-z]{3})")) {
                dateFormat = "yyyyddMMM";
            } else if (tempDate.matches("([a-z]{4})([0-31]{2})([0-9]{4})")) {
                dateFormat = "MMMMddyyyy";
            } else {
                System.out.println("The was not able to be formatted as expected!!!");
            }
        }

        Date guessedDate = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            guessedDate = formatter.parse(dateToGuess);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guessedDate;
    }

    public static String determineDateFormatPattern(String dateToGuess) {
        String tempDate = dateToGuess; //.replace("/", "").replace("-", "").replace(" ", "");
        String dateFormat = "";

        if (dateToGuess.contains("/")) {
            if (tempDate.matches("^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$")) {
                dateFormat = "MM/dd/yyyy";
            } else if (tempDate.matches("([0-31]{2})/([0-12]{2})/([0-9]{4})")) {
                dateFormat = "dd/MM/yyyy";
            } else if (tempDate.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
                dateFormat = "yyyy/MM/dd";
            } else if (tempDate.matches("^\\d{4}.\\d{1,2}.\\d{1,2}$")) {
                dateFormat = "yyyy/MM/dd";
            } else if (tempDate.matches("([a-z]{3})/([0-31]{2})/([0-9]{4})")) {
                dateFormat = "MMM/dd/yyyy";
            } else if (tempDate.matches("([0-9]{4})/([a-z]{3})/([0-31]{2})")) {
                dateFormat = "yyyy/MMM/dd";
            }
        } else if (dateToGuess.contains("-")) {
            if (tempDate.matches("^[0-3]?[0-9]-[0-3]?[0-9]-(?:[0-9]{2})?[0-9]{2}$")) {
                dateFormat = "MM-dd-yyyy";
            } else if (tempDate.matches("([0-31]{2})-([0-12]{2})-([0-9]{4})")) {
                dateFormat = "dd-MM-yyyy";
            } else if (tempDate.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                dateFormat = "yyyy-MM-dd";
            } else if (tempDate.matches("([a-z]{3})-([0-31]{2})-([0-9]{4})")) {
                dateFormat = "MMM-dd-yyyy";
            } else if (tempDate.matches("([0-9]{4})-([a-z]{3})-([0-31]{2})")) {
                dateFormat = "yyyy-MMM-dd";
            }
        } else if (dateToGuess.contains(".")) {
            if (tempDate.matches("^[0-3]?[0-9].[0-3]?[0-9].(?:[0-9]{2})?[0-9]{2}$")) {
                dateFormat = "MM.dd.yyyy";
            } else if (tempDate.matches("([0-31]{2}).([0-12]{2}).([0-9]{4})")) {
                dateFormat = "dd.MM.yyyy";
            } else if (tempDate.matches("([a-z]{3}).([0-31]{2}).([0-9]{4})")) {
                dateFormat = "MMM.dd.yyyy";
            } else if (tempDate.matches("([0-9]{4}).([a-z]{3}).([0-31]{2})")) {
                dateFormat = "yyyy.MMM.dd";
            }
        } else {
            if (tempDate.matches("([0-9]{4})([0-31]{2})([0-12]{2})")) {
                dateFormat = "yyyyddMM";
            } else if (tempDate.matches("([0-31]{2})([a-z]{3})([0-9]{4})")) {
                dateFormat = "ddMMMyyyy";
            } else if (tempDate.matches("([0-9]{4})([a-z]{3})([0-31]{2})")) {
                dateFormat = "yyyyMMMdd";
            } else if (tempDate.matches("([0-9]{4})([0-31]{2})([a-z]{3})")) {
                dateFormat = "yyyyddMMM";
            } else if (tempDate.matches("([a-z]{4})([0-31]{2})([0-9]{4})")) {
                dateFormat = "MMMMddyyyy";
            } else {
                System.out.println("The was not able to be formatted as expected!!!");
            }
        }
        return dateFormat;
    }

    private long calculateDaysWorkingTogether(Date s1, Date s2, Date e1, Date e2) {
        long daysSpentOnSameProject = 0;
        if ((s1.before(s2) || s1.equals(s2)) && ((e1.before(e2) || e1.equals(e2)))) {
            daysSpentOnSameProject = calculateDifference(s2, e1);
        } else if ((s1.after(s2) || s1.equals(s2)) && ((e1.after(e2) || e1.equals(e2)))) {
            daysSpentOnSameProject = calculateDifference(s1, e2);
        } else if ((s1.after(s2) || s1.equals(s2)) && ((e1.before(e2) || e1.equals(e2)))) {
            daysSpentOnSameProject = calculateDifference(s1, e1);
        } else if ((s1.before(s2) || s2.equals(s2)) && ((e1.before(e2) || e1.equals(e2)))) {
            daysSpentOnSameProject = calculateDifference(s2, e2);
        }

        long diff = daysSpentOnSameProject;
        long l = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        l = Math.abs(diff);
        return l;
    }

    private long calculateDifference(Date start, Date end) {
        Period period = Period.between(convertTolocalDate(start), convertTolocalDate(end));
        long result = ChronoUnit.DAYS.between(convertTolocalDate(start), convertTolocalDate(end));
        return result;
    }

    private LocalDate convertTolocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}