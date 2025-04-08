package de.greenstones.gsmr.msc.graph;

import java.util.List;
import java.util.Map;

import de.greenstones.gsmr.msc.graph.MscGraphService.RelatedNodes;
import lombok.AllArgsConstructor;

public interface GraphNodeService {
        List<Map<String, Object>> getNodes(String sourceType);

        Map<String, Object> getNode(String sourceType, String id);

        List<RelatedNodes> getAllRelatedNodes(String sourceType,
                        String... targetTypes);

        List<RelatedNodes> getRelatedNodes(String sourceType, String sourceId,
                        String... targetTypes);

        @AllArgsConstructor
        public static class DefaultGraphNodeService implements GraphNodeService {

                MscGraphService mscGraphService;
                String mscId;

                @Override
                public List<RelatedNodes> getRelatedNodes(String sourceType, String sourceId, String... targetTypes) {
                        return mscGraphService.getRelatedNodes(mscId, sourceType, sourceId, targetTypes);
                }

                @Override
                public List<RelatedNodes> getAllRelatedNodes(String sourceType, String... targetTypes) {
                        return mscGraphService.getAllRelatedNodes(mscId, sourceType, targetTypes);
                }

                @Override
                public Map<String, Object> getNode(String sourceType, String id) {
                        return mscGraphService.getNode(mscId, sourceType, id);
                }

                @Override
                public List<Map<String, Object>> getNodes(String sourceType) {
                        return mscGraphService.getNodes(mscId, sourceType);
                }

        }

}
