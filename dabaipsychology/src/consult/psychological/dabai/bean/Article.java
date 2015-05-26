package consult.psychological.dabai.bean;

public class Article {
	public String id;
	public String title;// 标题
	public String content;// 列表内容
	public String pcount;// 详细内容
	public String created;// 作者
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPcount() {
		return pcount;
	}

	public void setPcount(String pcount) {
		this.pcount = pcount;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

}