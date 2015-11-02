import java.util.ArrayList;
import java.util.List;

/**
 * POJO for curated data from a president's file
 */
public class President {
	private String name;
	private String contentPath;
	private List<String> infoBoxContent;
	private List<String> anchors;

	public President() {
		this.name = null;
		this.contentPath = null;
		this.infoBoxContent = new ArrayList<>();
		this.anchors = new ArrayList<>();
	}
	
	public President(
			String name, 
			String contentPath, 
			List<String> infoBoxContent, 
			List<String> anchors) {
		this.name = name;
		this.contentPath = contentPath;
		this.infoBoxContent = infoBoxContent;
		this.anchors = anchors;
	}
	
	public String getName() {
		return name;
	}

	public List<String> getInfoBoxContent() {
		return infoBoxContent;
	}

	public String getContentPath() {
		return contentPath;
	}
	
	public List<String> getAnchors() {
		return anchors;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInfoBoxContent(List<String> infoBoxContent) {
		this.infoBoxContent = infoBoxContent;
	}

	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	public void setAnchors(List<String> anchors) {
		this.anchors = anchors;
	}
}
