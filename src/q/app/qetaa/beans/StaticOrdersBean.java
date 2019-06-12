package q.app.qetaa.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value="staticOrdersBean")
@SessionScoped
public class StaticOrdersBean implements Serializable{

	private static final String WHATSAPP_PHONE_NUNBER = "966508448856";
	private static final long serialVersionUID = 1L;
	private String[] vehicle = new String[3];
	private String[] city = new String[3];
	private String[] part = new String[3];
	
	
	public StaticOrdersBean() {
		this.city[0] = randomCities.get((new Random()).nextInt(randomCities.size()));
		this.city[1] = randomCities.get((new Random()).nextInt(randomCities.size()));
		this.city[2] = randomCities.get((new Random()).nextInt(randomCities.size()));
		
		
		this.part[0] = randomParts.get((new Random()).nextInt(randomParts.size()));
		this.part[1] = randomParts.get((new Random()).nextInt(randomParts.size()));
		this.part[2] = randomParts.get((new Random()).nextInt(randomParts.size()));
		
		this.vehicle[0] = randomVehicles.get((new Random()).nextInt(randomVehicles.size()));
		this.vehicle[1] = randomVehicles.get((new Random()).nextInt(randomVehicles.size()));
		this.vehicle[2] = randomVehicles.get((new Random()).nextInt(randomVehicles.size()));
	}

	
	private static final List<String> randomVehicles = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add("فورد تورس");
			add("فورد اكسبدشن");
			add("فورد فلكس");
			add("فورد اكسبلورر");
			add("فورد ادج");
			add("فورد فوكس");
			add("فورد مونديو");
			add("هيونداي اكسنت");
			add("هيونداي سوناتا");
			add("هيونداي سنتافي");
			add("هيونداي أزيرا");
			add("هيونداي H1");
			add("كيا اوبتيما");
			add("مازدا 6");
			add("مازدا 3");
			add("مازدا CX9");
			add("مازدا CX7");
			add("جيلي EC7");
		}
	};
	
	private static final List<String> randomParts = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("صدام خلفي");
			add("لمبة ضباب");
			add("نور امامي يمين");
			add("نور أمامي يسار");
			add("كرسي قير سفلي");
			add("ِAC Filter");
			add("مساعدات خلفية و أمامية");
			add("كرسي قير علوي");
			add("زيت مكينة");
			add("حساس ABS");
			add("Thermostat valve");
			add("Chain cover gasket");
			add("صدام امامي");
			add("كشاف امامي يسار مع غطاء بلاستيك");
			add("كشاف امامي يسار");
			add("بطانة رفرف أمامي يسار");
			add("قاعدة صدام أمامي يسار");
			add("اذرعة امامية علوية");
			add("اذرعة خلفية علوية");
			add("عمود كرنك");
			add("ربلات عكوس امامية");
			add("فحمات امامية");
			add("فحمات خلفية");
			add("ثرموستات");
			add("غطاء راديتر");
			add("طرمبة زيت");
			add("طرمبة ماء");
			add("سلف");
			add("دينامو");
			add("هوز مكيف ضغط عالي");
			add("ثلاجة مكيف");
			add("فلتر هواء");
			add("ماء راديتر");
			add("كراسي مكينة");
			add("شبك امامي");
			add("كويلات");
			add("بواجي");
			add("طرمبة بنزين");
			add("سير مكينة");
			add("غطاء الهوك للصدام الأمامي");
			add("ثلاجة مكيف");
		}
	};
	
	private static final List<String> randomCities = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("جدة");
			add("الخبر");
			add("الدمام");
			add("الرياض");
			add("مكة المكرمة");
			add("المدينة المنورة");
			add("الجبيل");
			add("حائل");
			add("ينبع");
		}
	};


	public String[] getVehicle() {
		return vehicle;
	}



	public String[] getCity() {
		return city;
	}


	public String[] getPart() {
		return part;
	}
	
	
	public String getWhatsappLink() {
		return "https://wa.me/"+WHATSAPP_PHONE_NUNBER+"/";
	}

	
}
