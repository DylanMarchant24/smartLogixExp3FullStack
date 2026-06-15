package ${package}.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDTO {
    private List<Map<String, Object>> serviceA;
    private List<Map<String, Object>> serviceB;
}
