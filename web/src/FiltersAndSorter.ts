import {OptionWithStamp} from 'src/datatable-utils';
import {ApiDto} from 'src/store/microservices/types';

export function sortStringForSorter() {
  const converter: (OptionWithStamp) => string = a => a.label;
  return (a: OptionWithStamp, b: OptionWithStamp) => {
    const strA = converter(a) || '';
    const strB = converter(b) || '';
    return strA.localeCompare(strB);
  };
}

export function filteringByAttribute(attr: string) {
  if (!attr) {
    return l => l;
  }
  return (original: ApiDto[], filter: string) => {
    const upFilter = (!filter || filter.length === 0) ? '' : filter.toUpperCase();
    return original.filter((o) => {
      // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
      const value = `${o[attr]}`;
      const v = (value || '').toUpperCase();
      return v.indexOf(upFilter) !== -1;
    });
  };
}

export default function filtering(original: any[], filter: string) {
  if (!filter || filter.trim() === '') {
    return original;
  }

  const upFilter = filter.toUpperCase();
  return original
    .filter((p) => {
      const data = JSON.stringify(Object.values(p)).toUpperCase();
      return data.indexOf(upFilter) !== -1;
    });
}

export function sortString(str1, str2) {
  if (str1 < str2) {
    return -1;
  }

  if (str1 > str2) {
    return 1;
  }

  return 0;
}
