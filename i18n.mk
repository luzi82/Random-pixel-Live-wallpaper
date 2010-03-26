.PHONY : all

all : .i18n_timpstamp

.i18n_timpstamp : i18n.ods
	ods2xml.sh
	touch .i18n_timpstamp
