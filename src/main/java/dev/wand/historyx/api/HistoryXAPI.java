package dev.wand.historyx.api;

import dev.wand.historyx.api.extender.BanPluginExtender;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HistoryXAPI {

    private BanPluginExtender<?> extender;
}
