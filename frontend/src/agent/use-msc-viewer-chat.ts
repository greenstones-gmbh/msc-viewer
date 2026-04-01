import { LocalSession, useChat } from "@greenstones/qui-ai";
import { useNavigate, useParams } from "react-router-dom";
import { useConfigTypes } from "../msc/types/ConfigTypesContext";
import { agent } from "./agent";
import { MscViewerRunContext } from "./MscViewerRunContext";
import { MscCmdWidgets } from "./widgets/MscCmdWidgets";
import { QueryGraphFunctionCall } from "./tools/QueryGraphFunctionCall";

const session = new LocalSession("app-assistant");

export function useMscViewerChat() {
  const types = useConfigTypes();
  const navigate = useNavigate();
  const { mscId } = useParams();
  const chat = useChat<MscViewerRunContext>({
    agent,
    session,
    provider: () => {
      return {
        context: { navigate: (to: string) => navigate(`${to}`), types },
        state: {
          CURRENT_MSC_ID: mscId,
        },
      };
    },
    widgets: MscCmdWidgets,
    examples: [
      "find cell 10000",
      "show all lacs as table",
      "find gcas without gcref 500",
      "create cell 12345 with lac 300",
      "create gca 99888 with vbs groups 200 and 300",
    ],
    defaultFunctionCall: QueryGraphFunctionCall,
  });
  return chat;
}
