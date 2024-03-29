import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Lottely {
	static final String projectPath = System.getProperty("user.dir");

	static int allCnt = 0;
	static int[] num = new int[46];
	static int[] tens = new int[5];
	static int[] numWeight = new int[46];
	static int[] tensWeight = new int[5];
	static int[] result = new int[46];

	public static void main(String[] args) {
		Lottely temp = new Lottely();
		temp.init();
		
		int pickUpCnt = 6;
		int trainCnt = 140;
		int ballNo = 0;
		List<Integer> resultArr = new ArrayList<>();
		
		for (int i = 0; i < trainCnt; i++) {
			ballNo = temp.training();
			
			result[ballNo]++;
		}
		
		System.out.println(String.valueOf((int)(allCnt / 6) + 2) + "회 예상");
		System.out.println("----------------------------------------------------");
		
		int maxValue = 0;
		int idx = 0;
		
		for (int i = 0; i < pickUpCnt; i++) {
			maxValue = 0;
			for (int j = 1; j < result.length; j++) {
				if (maxValue < result[j]) {
					maxValue = result[j];
					idx = j;
				}
				//System.out.print(result[j] + " ");
			}
			//System.out.println();
			resultArr.add(idx);
			result[idx] = 0;
		}
		
		Collections.sort(resultArr);
		
		resultArr.stream().forEach(no -> System.out.print(no + " "));
	}
	
	void download() {
		try {
			Desktop.getDesktop().browse(new URI("https://dhlottery.co.kr/gameResult.do?method=byWin&wiselog=H_C_1_1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void init() {
		FileInputStream fis = null;
		File file = null;
		try {
			file = new File(String.valueOf(projectPath) + "\\resource\\excel.xls");
			String filePath = file.getAbsolutePath();
			fis = new FileInputStream(filePath);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			HSSFSheet sheet = workbook.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			for (int rowIndex = 4; rowIndex < rows; rowIndex++) {
				HSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					int cells = row.getPhysicalNumberOfCells();
					for (short columnIndex = 14; columnIndex < cells; columnIndex = (short) (columnIndex + 1)) {
						HSSFCell cell = row.getCell(columnIndex);
						allCnt++;
						num[(int) cell.getNumericCellValue()] = num[(int) cell.getNumericCellValue()] + 1;
						tens[(int) cell.getNumericCellValue() / 10] = tens[(int) cell.getNumericCellValue() / 10] + 1;
					}
				}
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		numWeight[1] = num[1];
		tensWeight[0] = tens[0];
		int i;
		for (i = 2; i < numWeight.length; i++)
			numWeight[i] = numWeight[i] + num[i] + numWeight[i - 1];
		for (i = 1; i < tensWeight.length; i++)
			tensWeight[i] = tensWeight[i] + tens[i] + tensWeight[i - 1];
	}

	void printPer() {
		DecimalFormat df = new DecimalFormat("0.000");
		int i;
		for (i = 1; i < num.length; i++)
			System.out.println(String.valueOf(i) + ": \t" + num[i] + ",\t\t " + df.format(num[i] / allCnt * 100.0D)
					+ "\t " + numWeight[i]);
		System.out.println("----------------------------------------------------");
		for (i = 0; i < tens.length; i++)
			System.out.println("" + i + "" + tens[i] + ",\t " + df.format(tens[i] / allCnt * 100.0D));
	}

	void printArr(int[] arr) {
		for (int i = 0; i < arr.length; i++)
			System.out.println("i: " + i + ", value: " + arr[i]);
	}
	
	void print(int[] data) {
		
		for (int number : data) {
			System.out.print((new StringBuilder()).append(number).append("\t").toString());
		}
		System.out.println();
	}

	int training() {
		int lRandom = 0;
		int nRandom = 0;
		int tmpVal = 0;
		
		int len = 0;
		int minRnd = 0;
		int maxRnd = numWeight[45];
		lRandom = (int) (Math.random() * tensWeight[4]) + 1;
		
		for (int j = 1; j < 5 && lRandom > tensWeight[j - 1]; j++)
			len = j;
		
		switch (len) {
		case 1:
			minRnd = numWeight[10];
			maxRnd = numWeight[20];
			break;
		case 2:
			minRnd = numWeight[20];
			maxRnd = numWeight[30];
			break;
		case 3:
			minRnd = numWeight[30];
			maxRnd = numWeight[40];
			break;
		case 4:
			minRnd = numWeight[40];
			maxRnd = numWeight[45];
			break;
		default:
			maxRnd = numWeight[10];
			break;
		}

		nRandom = (int) (Math.random() * (maxRnd - minRnd)) + minRnd;

		for (int j = len * 10; nRandom > numWeight[j]; j++)
			tmpVal = j;

		if (tmpVal == 0)
			tmpVal = 45;

		return tmpVal;
	}
	
}
