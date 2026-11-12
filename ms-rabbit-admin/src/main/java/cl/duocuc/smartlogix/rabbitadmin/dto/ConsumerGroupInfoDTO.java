package cl.duocuc.smartlogix.rabbitadmin.dto;

public class ConsumerGroupInfoDTO {
    private String groupId;

    public ConsumerGroupInfoDTO(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
}