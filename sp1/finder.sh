echo "Скрипт, который удаляет все файлы больше определенного размера"
echo "С помощью данного скрипта можно найти все файлы, размер которых больше введённого пользователем, затем эти файлы можно удалить"
echo "Разработчик: Остапенко Роман"
while true; do
    echo "Введите каталог для поиска:"
    read path
    echo "Выберите единицы размерности:"
    echo "1) c - байты," 
    echo "2) k - килобайты,"
    echo "3) M - мегабайты," 
    echo "4) G - гигабайты"
    while true; do
        read opt
	case $opt in
            1) type="c"; str="байтах:"; break;;
            2) type="k"; str="килобайтах:"; break;;
	    3) type="M"; str="мегабайтах:"; break;;
	    4) type="G"; str="гигабайтах:"; break;;
	    *) echo "Необходимо выбрать из списка!";;
	esac
    done

    echo "Введите размер для поиска в "$str
    read sz
    fl=$(find $path -not -path '*/\.*' -type f -size +$sz$type)
    if [ "$fl" = "" ]; then 
        echo "Файлы не найдены"
    else
        echo $fl
        echo "Хотите удалить найденные файлы?"
        read yn
	while true; do
	    case $yn in
	    [Yy]* ) find $path -not -path '*/\.*' -type f -size +$sz$type -delete; break;;
	    [Nn]* ) echo "Удаление отменено"; break;;
		* ) echo "Пожалуйста введите y - да или n - нет";;
	    esac
	done
    fi	
    echo "Продолжить работу со скриптом?"
    read yn
    while true; do
        case $yn in
            [Yy]* ) echo "Продолжаем работу"; f=0; break;;
            [Nn]* ) echo "Выход из скрипта"; f=1; break;;
                * ) echo "Пожалуйста введите y - да или n - нет";;
        esac
    done
    if [ $f -eq 1 ]; then break
    fi
done

